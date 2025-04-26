package com.bank.services.users;

import com.bank.exceptions.DomainException;
import com.bank.utils.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;

@Service
public class CaptchaService {
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 5;
    private static final int CAPTCHA_WIDTH = 160;
    private static final int CAPTCHA_HEIGHT = 50;
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final List<Font> FONTS = List.of(
            new Font("Arial", Font.BOLD, 40),
            new Font("Verdana", Font.BOLD, 40),
            new Font("Tahoma", Font.BOLD, 40)
    );

    @Value("${security.jwt.captcha-token.secret-key}")
    private String captchaTokenSecretKet;

    @Value("${security.jwt.captcha-token.expiration-time}")
    private Integer captchaTokenExpiration;

    public Map<String, String> generateCaptcha() {
        var captchaText = generateCaptchaText();
        var image = generateCaptchaImage4(captchaText);

        var outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            throw new DomainException("error.auth.captcha.error");
        }

        var base64Image = BASE64_ENCODER.encodeToString(outputStream.toByteArray());

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

        return correctCaptcha.trim().equalsIgnoreCase(captchaAnswer);
    }

//    private BufferedImage generateCaptchaImage(String text) {
//        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
//        var g2d = image.createGraphics();
//
//        g2d.setColor(Color.WHITE);
//        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
//        g2d.setFont(new Font("Arial", Font.BOLD, 40));
//        g2d.setColor(Color.BLUE);
//        g2d.drawString(text, 20, 45);
//        g2d.dispose();
//
//        return image;
//    }
//
//    private BufferedImage generateCaptchaImage2(String text) {
//        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
//        var g2d = image.createGraphics();
//
//        g2d.setColor(new Color(240, 240, 240));
//        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
//
//        for (var i = 0; i < 10; i++) {
//            g2d.setColor(generateRandomColor());
//            int x1 = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
//            int y1 = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
//            int x2 = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
//            int y2 = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
//            g2d.drawLine(x1, y1, x2, y2);
//        }
//
//        g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 40));
//        for (var i = 0; i < text.length(); i++) {
//            g2d.setColor(generateRandomColor());
//            var theta = (SECURE_RANDOM.nextDouble() - 0.5) * 0.4;
//            g2d.rotate(theta, 30 * i + 15, 45);
//            g2d.drawString(String.valueOf(text.charAt(i)), 30 * i + 10, 45);
//            g2d.rotate(-theta, 30 * i + 15, 45);
//        }
//
//        g2d.dispose();
//
//        return image;
//    }

    private BufferedImage generateCaptchaImage3(String text) {
        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        var g2d = image.createGraphics();

        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        for (var i = 0; i < 10; i++) {
            g2d.setColor(generateRandomColor());
            int x1 = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
            int y1 = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
            int x2 = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
            int y2 = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 40));
        for (var i = 0; i < text.length(); i++) {
            g2d.setColor(generateRandomColor());
            var theta = (SECURE_RANDOM.nextDouble() - 0.5) * 0.4;
            g2d.rotate(theta, 30 * i + 15, 45);
            g2d.drawString(String.valueOf(text.charAt(i)), 30 * i + 10, 45);
            g2d.rotate(-theta, 30 * i + 15, 45);
        }

        int frames = 20;
        int period = SECURE_RANDOM.nextInt(10) + 5;
        int phase = SECURE_RANDOM.nextInt(2);
        for (int i = 0; i < CAPTCHA_HEIGHT; i++) {
            double d = (double) (period >> 1) *
                    Math.sin((double) i / (double) period
                            + (6.2831853071795862D * (double) phase) / (double) frames);
            g2d.copyArea(0, i, CAPTCHA_WIDTH, 1, (int) d, 0);
            g2d.setColor(new Color(240, 240, 240));
            g2d.drawLine((int) d, i, 0, i);
            g2d.drawLine((int) d + CAPTCHA_WIDTH, i, CAPTCHA_WIDTH, i);
        }

        g2d.dispose();

        return image;
    }

    private BufferedImage generateCaptchaImage4(String text) {
        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        var g2d = image.createGraphics();

        // Draw background gradient
        GradientPaint gp = new GradientPaint(0, 0,
                new Color(SECURE_RANDOM.nextInt(100) + 155, SECURE_RANDOM.nextInt(100) + 155, SECURE_RANDOM.nextInt(100) + 155),
                CAPTCHA_WIDTH, CAPTCHA_HEIGHT,
                new Color(SECURE_RANDOM.nextInt(100) + 155, SECURE_RANDOM.nextInt(100) + 155, SECURE_RANDOM.nextInt(100) + 155));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        // Draw random noise
        for (int i = 0; i < 300; i++) {
            int x = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
            int y = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
            int rgb = new Color(
                    SECURE_RANDOM.nextInt(255), SECURE_RANDOM.nextInt(255), SECURE_RANDOM.nextInt(255)).getRGB();
            image.setRGB(x, y, rgb);
        }

        for (int i = 0; i < 10; i++) {
            g2d.setColor(generateRandomColor());
            int w = SECURE_RANDOM.nextInt(30);
            int h = SECURE_RANDOM.nextInt(30);
            int x = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH - w);
            int y = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT - h);
            g2d.fill(new Ellipse2D.Double(x, y, w, h));
        }

        int xGap = CAPTCHA_WIDTH / (text.length() + 2);
        for (int i = 0; i < text.length(); i++) {
            g2d.setFont(FONTS.get(SECURE_RANDOM.nextInt(FONTS.size())));
            g2d.setColor(generateRandomColor());
            double theta = (SECURE_RANDOM.nextDouble() - 0.5) * 0.5;
            g2d.rotate(theta, (i + 1) * xGap, CAPTCHA_HEIGHT / 2);
            g2d.drawString(String.valueOf(text.charAt(i)), (i + 1) * xGap - 10, CAPTCHA_HEIGHT / 2 + 10);
            g2d.rotate(-theta, (i + 1) * xGap, CAPTCHA_HEIGHT / 2);
        }

        g2d.dispose();

        return image;
    }

    private String generateCaptchaText() {
        var captchaCharsLength = CAPTCHA_CHARS.length();
        var captchaStr = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captchaStr.append(CAPTCHA_CHARS.charAt(SECURE_RANDOM.nextInt(captchaCharsLength)));
        }
        return captchaStr.toString();
    }

    private Color generateRandomColor() {
        return new Color(
                SECURE_RANDOM.nextInt(200),
                SECURE_RANDOM.nextInt(200),
                SECURE_RANDOM.nextInt(200)
        );
    }
}
