package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.request.RegisterRequest;
import com.mareninss.blogapi.api.response.ErrorsResponse;
import com.mareninss.blogapi.dao.CaptchaRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.entity.CaptchaCode;
import com.mareninss.blogapi.entity.User;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class RegisterServiceImpl implements RegisterService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CaptchaRepository captchaRepository;

  private final ErrorsResponse errorsResponse;

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

  private boolean hasErrors(ErrorDto errors) {
    return errors.getName() != null || errors.getPassword() != null || errors.getEmail() != null
        || errors.getCaptcha() != null;
  }
}
