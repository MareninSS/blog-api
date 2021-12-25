package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.EditProfileRequest;
import com.mareninss.blogapi.api.request.PasswordResetRequest;
import com.mareninss.blogapi.api.request.RecoverRequest;
import com.mareninss.blogapi.api.request.RegisterRequest;
import com.mareninss.blogapi.api.response.ErrorsResponse;
import com.mareninss.blogapi.dao.CaptchaRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.entity.CaptchaCode;
import com.mareninss.blogapi.entity.User;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.Principal;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Component
public class RegisterServiceImpl implements RegisterService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CaptchaRepository captchaRepository;
  @Autowired
  private JavaMailSender mailSender;

  private final ErrorsResponse errorsResponse;

  private final Path root = Paths.get("upload");

  @Value("${hostname.prefix}")
  private String hostName;

  @Value("${link.lifetime}")
  private int linkLifetime;

  public RegisterServiceImpl() {
    errorsResponse = new ErrorsResponse();
  }

  @Override
  public ErrorsResponse createUser(RegisterRequest registerRequest) {
    final Integer notModerator = 0;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    ErrorDto errorDto = new ErrorDto();
    String email = registerRequest.getEmail();
    Optional<User> user = userRepository.findByEmail(email);
    CaptchaCode captchaCode = captchaRepository.findByCode(registerRequest.getCode());
    boolean isNameValid = registerRequest.getName().length() >= 2;
    boolean isPasswordValid = registerRequest.getPassword().length() >= 6;
    boolean isCaptchaValid = captchaCode != null && Objects.equals(captchaCode.getSecretCode(),
        registerRequest.getSecrete());
    if (user.isPresent()) {
      errorDto.setEmail("Этот e-mail уже зарегистрирован");
    }
    if (!isNameValid) {
      errorDto.setName("Имя указано неверно");
    }
    if (!isPasswordValid) {
      errorDto.setPassword("Пароль короче 6 символов");
    }
    if (!isCaptchaValid) {
      errorDto.setCaptcha("Код с картинки введен неверно");
    }
    if (hasErrors(errorDto)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(errorDto);
      return errorsResponse;
    }

    User userNew = new User();
    userNew.setIsModerator(notModerator);
    userNew.setEmail(registerRequest.getEmail());
    userNew.setName(registerRequest.getName());
    userNew.setRegTime(new Date());
    userNew.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
    errorsResponse.setResult(true);

    userRepository.saveAndFlush(userNew);

    return errorsResponse;

  }

  @Override
  public ErrorsResponse editProfileJSON(EditProfileRequest request, Principal principal) {
    if (principal != null) {
      User currentUser = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      if (request.getPhoto() == null && request.getRemovePhoto() == null
          && request.getPassword() != null) {
        return changeOnlyPassword(request, principal);
      }
      if (request.getPhoto() == null && request.getRemovePhoto() == null
          && request.getPassword() == null) {
        currentUser.setName(request.getName());
        currentUser.setEmail(request.getEmail());
        userRepository.saveAndFlush(currentUser);
        errorsResponse.setResult(true);
        return errorsResponse;
      }
      if (Objects.equals(request.getEmail(), currentUser.getEmail())
          && request.getRemovePhoto() == 1) {
        return removePhoto(request, principal);
      }
    } else {
      errorsResponse.setResult(false);
    }
    return errorsResponse;

  }

  @Override
  public ErrorsResponse editProfileMFD(MultipartFile photo, String name, String email,
      String password, Integer removePhoto, Principal principal) {
    if (principal != null) {

      if (removePhoto == 0 && photo != null && name != null
          && password != null && email != null) {
        return changePasswordAndPhoto(photo, password, principal);
      }
      if (removePhoto == 0 && photo != null && name != null
          && email != null) {
        return changePhotoAndData(photo, name, email, principal);
      }

    } else {
      errorsResponse.setResult(false);
    }
    return errorsResponse;
  }


  private boolean hasErrors(ErrorDto errors) {
    return errors.getName() != null || errors.getPassword() != null || errors.getEmail() != null
        || errors.getCaptcha() != null || errors.getCode() != null;
  }

  private ErrorsResponse changeOnlyPassword(EditProfileRequest request, Principal principal) {
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    ErrorDto errorDto = new ErrorDto();
    boolean isNameValid = request.getName().length() >= 2;
    boolean isPasswordValid = request.getPassword().length() >= 6;
    if (!isNameValid) {
      errorDto.setName("Имя указано неверно");
    }
    if (!isPasswordValid) {
      errorDto.setPassword("Пароль короче 6 символов");
    }
    if (hasErrors(errorDto)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(errorDto);
      return errorsResponse;
    }
    currentUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
    userRepository.saveAndFlush(currentUser);
    errorsResponse.setResult(true);
    return errorsResponse;
  }

  private ErrorsResponse changePasswordAndPhoto(MultipartFile photo,
      String password, Principal principal) {
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    ErrorDto error = new ErrorDto();
    if (photo.getSize() > 2097152) {
      error.setImage("размер файла больше 2.0 МБ");
    }
    if (!FilenameUtils.isExtension(
        photo.getOriginalFilename(), "jpg", "png")) {
      error.setExtension("файл не формата jpg, png");
    }
    if (photo.getSize() > 2097152) {
      error.setImage("Размер фото слишком большое, нужно не более 2.0 МБ");
    }
    if (hasErrors(error)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(error);
      return errorsResponse;
    }
    String path = null;
    try {
      path = resizeFile(photo);
    } catch (IOException e) {
      e.printStackTrace();
    }
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    currentUser.setPassword(bCryptPasswordEncoder.encode(password));
    currentUser.setPhoto(path);
    userRepository.saveAndFlush(currentUser);
    errorsResponse.setResult(true);
    return errorsResponse;
  }

  private ErrorsResponse changePhotoAndData(MultipartFile photo, String name, String email,
      Principal principal) {
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    ErrorDto error = new ErrorDto();
    if (photo.getSize() > 2097152) {
      error.setImage("размер файла больше 2.0 МБ");
    }
    if (!FilenameUtils.isExtension(
        photo.getOriginalFilename(), "jpg", "png")) {
      error.setExtension("файл не формата jpg, png");
    }
    if (photo.getSize() > 2097152) {
      error.setImage("Размер фото слишком большое, нужно не более 2.0 МБ");
    }
    if (hasErrors(error)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(error);
      return errorsResponse;
    }

    String path = null;
    try {
      path = resizeFile(photo);
    } catch (IOException e) {
      e.printStackTrace();
    }

    currentUser.setPhoto(path);
    currentUser.setName(name);
    currentUser.setEmail(email);
    userRepository.saveAndFlush(currentUser);
    errorsResponse.setResult(true);
    return errorsResponse;
  }

  private ErrorsResponse removePhoto(EditProfileRequest request, Principal principal) {
    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    String pathFromBD = currentUser.getPhoto();
    Path pathTODel = Paths.get(pathFromBD).toAbsolutePath();
    try {
      Files.walkFileTree(pathTODel, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    currentUser.setPhoto(request.getPhoto());
    userRepository.saveAndFlush(currentUser);
    errorsResponse.setResult(true);
    return errorsResponse;
  }

  private String resizeFile(MultipartFile file)
      throws IOException {
    final int WIDTH = 36;
    final int HEIGHT = 36;
    Path path = root.resolve(Path.of("photos"));
    Files.createDirectories(path);
    String p = path.resolve(Objects.requireNonNull(file.getOriginalFilename())).toString();
    BufferedImage imageResized = Scalr.resize(ImageIO.read(file.getInputStream()), WIDTH, HEIGHT);
    ImageIO.write(imageResized, "jpg", new File(p));
    return p;
  }

  @Override
  public Map<String, Boolean> recoverPass(RecoverRequest email) {
    Optional<User> user = userRepository.findByEmail(email.getEmail());
    Map<String, Boolean> result = new HashMap<>();
    if (user.isPresent()) {
      String token = UUID.randomUUID().toString();
      final String LINK = hostName + "/login/change-password/" + token;
      user.get().setCode(token);
      user.get().setTimeCode(add(new Date(), linkLifetime));
      userRepository.saveAndFlush(user.get());
      try {
        sendEmail(email.getEmail(), LINK);
      } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      result.put("result", true);
    } else {
      result.put("result", false);
    }
    return result;
  }

  private Date add(Date date, int minute) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE, minute);
    return calendar.getTime();
  }

  public void sendEmail(String recipientEmail, String link)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom("devpubhelper@gmail.com", "DevPub Support");
    helper.setTo(recipientEmail);

    String subject = "Here's the link to reset your password";

    String content = "<p>Hello,</p>"
        + "<p>You have requested to reset your password.</p>"
        + "<p>Click the link below to change your password:</p>"
        + "<p><a href=\"" + link + "\">Change my password</a></p>"
        + "<br>"
        + "<p>Ignore this email if you do remember your password, "
        + "or you have not made the request.</p>";

    helper.setSubject(subject);

    helper.setText(content, true);

    mailSender.send(message);
  }

  @Override
  public ErrorsResponse resetPassword(PasswordResetRequest request) {
    if (request != null) {
      CaptchaCode captchaCode = captchaRepository.findByCode(request.getCaptcha());
      boolean isPasswordValid = request.getPassword().length() >= 6;
      boolean isCaptchaValid = captchaCode != null && Objects.equals(captchaCode.getSecretCode(),
          request.getSecrete());
      Optional<User> optionalUser = userRepository.findByCode(request.getCode());
      boolean isLinkValid =
          optionalUser.isPresent() && optionalUser.get().getTimeCode().before(new Date());
      boolean isCodeCorrect =
          optionalUser.isPresent() && optionalUser.get().getCode().equals(request.getCode());
      final String LINK = hostName + "/login/restore-password";
      ErrorDto errorDto = new ErrorDto();

      if (!isPasswordValid) {
        errorDto.setPassword("Пароль короче 6 символов");
      }
      if (!isCaptchaValid) {
        errorDto.setCaptcha("Код с картинки введен неверно");
      }
      if (isLinkValid) {
        errorDto.setCode("Ссылка для восстановления пароля устарела. \n"
            + "<a href=\"" + LINK + "\">Запросить ссылку снова</a>");
      }
      if (!isCodeCorrect) {
        errorDto.setCode("неверный код восстановления пароля");
      }
      if (hasErrors(errorDto)) {
        errorsResponse.setResult(false);
        errorsResponse.setErrors(errorDto);
        return errorsResponse;
      }
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      optionalUser.orElseThrow(() -> new UsernameNotFoundException(""))
          .setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
      userRepository.saveAndFlush(optionalUser.get());
      errorsResponse.setResult(true);
      return errorsResponse;
    }
    errorsResponse.setResult(false);
    return errorsResponse;
  }
}
