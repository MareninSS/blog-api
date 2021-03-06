package com.mareninss.blogapi.service.service_impl;

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
import com.mareninss.blogapi.service.FileStorageService;
import com.mareninss.blogapi.service.RegisterService;
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

  @Autowired
  private FileStorageService fileStorageService;

  private final Path root = Paths.get("src/main/resources/upload");

  @Value("${hostname.prefix}")
  private String hostName;

  @Value("${link.lifetime}")
  private int linkLifetime;

  public RegisterServiceImpl() {

  }

  @Override
  public ErrorsResponse createUser(RegisterRequest registerRequest) {
    ErrorsResponse errorsResponse = new ErrorsResponse();

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
      errorDto.setEmail("???????? e-mail ?????? ??????????????????????????????");
    }
    if (!isNameValid) {
      errorDto.setName("?????? ?????????????? ??????????????");
    }
    if (!isPasswordValid) {
      errorDto.setPassword("???????????? ???????????? 6 ????????????????");
    }
    if (!isCaptchaValid) {
      errorDto.setCaptcha("?????? ?? ???????????????? ???????????? ??????????????");
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
    ErrorsResponse errorsResponse = new ErrorsResponse();

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
    ErrorsResponse errorsResponse = new ErrorsResponse();

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
    ErrorsResponse errorsResponse = new ErrorsResponse();

    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    ErrorDto errorDto = new ErrorDto();
    boolean isNameValid = request.getName().length() >= 2;
    boolean isPasswordValid = request.getPassword().length() >= 6;
    if (!isNameValid) {
      errorDto.setName("?????? ?????????????? ??????????????");
    }
    if (!isPasswordValid) {
      errorDto.setPassword("???????????? ???????????? 6 ????????????????");
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
    ErrorsResponse errorsResponse = new ErrorsResponse();

    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    ErrorDto error = new ErrorDto();
    if (photo.getSize() > 2097152) {
      error.setImage("???????????? ?????????? ???????????? 2.0 ????");
    }
    if (!FilenameUtils.isExtension(
        photo.getOriginalFilename(), "jpg", "png")) {
      error.setExtension("???????? ???? ?????????????? jpg, png");
    }
    if (photo.getSize() > 2097152) {
      error.setImage("???????????? ???????? ?????????????? ??????????????, ?????????? ???? ?????????? 2.0 ????");
    }
    if (hasErrors(error)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(error);
      return errorsResponse;
    }
    String fileName = null;
    try {
      fileName = resizeFile(photo);
    } catch (IOException e) {
      e.printStackTrace();
    }
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    currentUser.setPassword(bCryptPasswordEncoder.encode(password));
    String url = "/api/image/";
    currentUser.setPhoto(url + fileName);
    userRepository.saveAndFlush(currentUser);
    errorsResponse.setResult(true);
    return errorsResponse;
  }

  private ErrorsResponse changePhotoAndData(MultipartFile photo, String name, String email,
      Principal principal) {
    ErrorsResponse errorsResponse = new ErrorsResponse();

    com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
            principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    ErrorDto error = new ErrorDto();
    if (photo.getSize() > 2097152) {
      error.setImage("???????????? ?????????? ???????????? 2.0 ????");
    }
    if (!FilenameUtils.isExtension(
        photo.getOriginalFilename(), "jpg", "png")) {
      error.setExtension("???????? ???? ?????????????? jpg, png");
    }
    if (photo.getSize() > 2097152) {
      error.setImage("???????????? ???????? ?????????????? ??????????????, ?????????? ???? ?????????? 2.0 ????");
    }
    if (hasErrors(error)) {
      errorsResponse.setResult(false);
      errorsResponse.setErrors(error);
      return errorsResponse;
    }

    String fileName = null;
    try {
      fileName = resizeFile(photo);
    } catch (IOException e) {
      e.printStackTrace();
    }

    String url = "/api/image/";
    currentUser.setPhoto(url + fileName);
    currentUser.setName(name);
    currentUser.setEmail(email);
    userRepository.saveAndFlush(currentUser);
    errorsResponse.setResult(true);
    return errorsResponse;
  }

  private ErrorsResponse removePhoto(EditProfileRequest request, Principal principal) {
    ErrorsResponse errorsResponse = new ErrorsResponse();

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
          return FileVisitResult.TERMINATE;
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
    final int WIDTH = 360;
    final int HEIGHT = 360;
    String uuidFile = UUID.randomUUID() + "." + file.getOriginalFilename();
    Path path = root.resolve(fileStorageService.createFolderName(uuidFile));
    Files.createDirectories(path);
    BufferedImage imageResized = Scalr.resize(ImageIO.read(file.getInputStream()), WIDTH, HEIGHT);
    ImageIO.write(imageResized, "jpg", new File(path.resolve(uuidFile).toString()));
    return uuidFile;
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
    ErrorsResponse errorsResponse = new ErrorsResponse();

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
        errorDto.setPassword("???????????? ???????????? 6 ????????????????");
      }
      if (!isCaptchaValid) {
        errorDto.setCaptcha("?????? ?? ???????????????? ???????????? ??????????????");
      }
      if (isLinkValid) {
        errorDto.setCode("???????????? ?????? ???????????????????????????? ???????????? ????????????????. \n"
            + "<a href=\"" + LINK + "\">?????????????????? ???????????? ??????????</a>");
      }
      if (!isCodeCorrect) {
        errorDto.setCode("???????????????? ?????? ???????????????????????????? ????????????");
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
