package com.mareninss.blogapi.service;

import com.github.cage.Cage;
import com.github.cage.YCage;
import com.github.cage.image.Painter;
import com.mareninss.blogapi.api.response.CaptchaResponse;
import com.mareninss.blogapi.dao.CaptchaRepository;
import com.mareninss.blogapi.entity.CaptchaCode;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CaptchaServiceImpl implements CaptchaService {

  @Autowired
  private CaptchaRepository captchaRepository;
  private Cage cage;
  private final Date CURRENT_TIME = new Date();
  private final CaptchaResponse captchaResponse;
  @Value("${captcha.timeToDel}")
  private int timeToDelete;

  public CaptchaServiceImpl() {
    captchaResponse = new CaptchaResponse();
  }

  @Override
  public CaptchaResponse generateCaptcha() {
    Painter painter = new Painter(200, 100, null, null, null, null);
    cage = new Cage(painter, null, null, null, null, null, null);
    String namePrefix = "data:image/png;base64, ";
    String code = cage.getTokenGenerator().next(); // получаю текст картинки
    byte[] imageBuff = cage.draw(
        code); // получаю массив байтов, содержащий сериализованное сгенерированное изображение
    String enCodeBase64 = Base64.getEncoder().encodeToString(imageBuff);//конвертируем в Base64
    String image = namePrefix + enCodeBase64;//
    String secret = encodeToSecreteCode(code);

    CaptchaCode captchaCode = new CaptchaCode();
    captchaCode.setTime(CURRENT_TIME);
    captchaCode.setCode(code);
    captchaCode.setSecretCode(secret);

    captchaRepository.saveAndFlush(captchaCode);

    // TODO: 29.11.2021 реализация удаления из БД устаревшие капчи

    captchaResponse.setSecret(secret);
    captchaResponse.setImage(image);
    return captchaResponse;
  }

  private String encodeToSecreteCode(String code) {
    return Base64.getEncoder().encodeToString(code.getBytes());
  }

  private String decodeToCode(String secreteCode) {
    byte[] decodedBytes = Base64.getDecoder().decode(secreteCode);
    return new String(decodedBytes);
  }
}
