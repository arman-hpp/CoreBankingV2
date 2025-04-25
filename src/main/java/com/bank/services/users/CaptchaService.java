package com.bank.services.users;

import com.bank.exceptions.DomainException;
import com.bank.utils.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class CaptchaService {
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 5;
    private static final int CAPTCHA_WIDTH = 160;
    private static final int CAPTCHA_HEIGHT = 50;

    @Value("${security.jwt.captcha-token.secret-key}")
    private String captchaTokenSecretKet;

    @Value("${security.jwt.captcha-token.expiration-time}")
    private Integer captchaTokenExpiration;

    public Map<String, String> generateCaptcha() {
        var captchaText = generateCaptchaText();

        var image = generateCaptchaImage2(captchaText);
        var outputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            throw new DomainException("error.auth.captcha.error");
        }

        var base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        Map<String, Object> claims = new HashMap<>();
        claims.put("captcha", captchaText);

        var captchaToken = JwtUtils.generateToken(
                "CAPTCHA", captchaTokenExpiration, captchaTokenSecretKet, claims);

        Map<String, String> response = new HashMap<>();
        response.put("image", "data:image/png;base64," + base64Image);
        response.put("token", captchaToken);

        return response;
    }

    public boolean VerifyCaptcha(String captchaToken, String captchaAnswer) {
        if(!JwtUtils.isTokenValid(captchaToken, captchaTokenSecretKet)) {
            return false;
        }

        var correctCaptcha = JwtUtils.extractClaim(captchaToken, captchaTokenSecretKet,
                claims -> (String) claims.get("captcha"));

        return correctCaptcha.equalsIgnoreCase(captchaAnswer);
    }

    private BufferedImage generateCaptchaImage(String text) {
        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Color.BLUE);
        g2d.drawString(text, 20, 45);
        g2d.dispose();

        return image;
    }

    private BufferedImage generateCaptchaImage2(String text) {
        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        var g2d = image.createGraphics();

        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        var random = new Random();
        for (var i = 0; i < 10; i++) {
            g2d.setColor(generateRandomColor());
            int x1 = random.nextInt(CAPTCHA_WIDTH);
            int y1 = random.nextInt(CAPTCHA_HEIGHT);
            int x2 = random.nextInt(CAPTCHA_WIDTH);
            int y2 = random.nextInt(CAPTCHA_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 40));
        for (var i = 0; i < text.length(); i++) {
            g2d.setColor(generateRandomColor());
            var theta = (random.nextDouble() - 0.5) * 0.4;
            g2d.rotate(theta, 30 * i + 15, 45);
            g2d.drawString(String.valueOf(text.charAt(i)), 30 * i + 10, 45);
            g2d.rotate(-theta, 30 * i + 15, 45);
        }

        g2d.dispose();

        return image;
    }

    private String generateCaptchaText() {
        var random = new Random();
        var captchaCharsLength = CAPTCHA_CHARS.length();
        var captchaStr = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captchaStr.append(CAPTCHA_CHARS.charAt(random.nextInt(captchaCharsLength)));
        }
        return captchaStr.toString();
    }

    private Color generateRandomColor() {
        var random = new Random();
        return new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));
    }
}
