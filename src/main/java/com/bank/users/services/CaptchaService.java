package com.bank.users.services;

import com.bank.core.exceptions.BusinessException;
import com.bank.core.utils.JwtUtils;
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
        var captchaText = createRandomCaptchaText();
        var captchaImage = createCaptchaImage(captchaText);
        var base64Image = convertImageToBase64(captchaImage);

        var captchaToken = JwtUtils.generateToken(
                "CAPTCHA", captchaTokenExpiration, captchaTokenSecretKet, Map.of("captcha", captchaText));

        return Map.of(
                "image", "data:image/png;base64," + base64Image,
                "token", captchaToken
        );
    }

    public boolean verifyCaptcha(String captchaToken, String captchaAnswer) {
        if(!JwtUtils.isTokenValid(captchaToken, captchaTokenSecretKet)) {
            return false;
        }

        var correctCaptcha = JwtUtils.extractClaim(captchaToken, captchaTokenSecretKet,
                claims -> (String) claims.get("captcha"));

        return correctCaptcha.trim().equalsIgnoreCase(captchaAnswer);
    }

    private static String createRandomCaptchaText() {
        var captchaCharsLength = CAPTCHA_CHARS.length();
        var captchaStr = new StringBuilder();
        for (var i = 0; i < CAPTCHA_LENGTH; i++) {
            captchaStr.append(CAPTCHA_CHARS.charAt(SECURE_RANDOM.nextInt(captchaCharsLength)));
        }
        return captchaStr.toString();
    }

    private BufferedImage createCaptchaImage(String text) {
        var image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        var g2d = image.createGraphics();

        try {
            drawBackground(g2d);
            drawCaptchaText(g2d, text);
            drawNoise(g2d);
            applyShear(image);
        } finally {
            g2d.dispose();
        }

        return image;
    }

    private void drawBackground(Graphics2D g2d) {
        /*
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        */

        var gp = new GradientPaint(
                0, 0,
                generateLightColor(),
                CAPTCHA_WIDTH, CAPTCHA_HEIGHT,
                generateLightColor());
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
    }

    private void drawCaptchaText(Graphics2D g2d, String text) {
        var xGap = CAPTCHA_WIDTH / (text.length() + 2);
        for (var i = 0; i < text.length(); i++) {
            g2d.setFont(FONTS.get(SECURE_RANDOM.nextInt(FONTS.size())));
            g2d.setColor(generateRandomColor());
            var theta = (SECURE_RANDOM.nextDouble() - 0.5) * 0.5;
            g2d.rotate(theta, (i + 1) * xGap, (double) CAPTCHA_HEIGHT / 2);
            g2d.drawString(String.valueOf(text.charAt(i)), (i + 1) * xGap - 10, CAPTCHA_HEIGHT / 2 + 10);
            g2d.rotate(-theta, (i + 1) * xGap, (double) CAPTCHA_HEIGHT / 2);
        }
    }

    private void drawNoise(Graphics2D g2d) {
        // Draw random ellipses
        for (var i = 0; i < 10; i++) {
            var w = SECURE_RANDOM.nextInt(10);
            var h = SECURE_RANDOM.nextInt(10);
            var x = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH - w);
            var y = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT - h);
            g2d.setColor(generateRandomColor());
            g2d.fill(new Ellipse2D.Double(x, y, w, h));
        }

        // Draw random lines
        for (var i = 0; i < 10; i++) {
            var x1 = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
            var y1 = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
            var x2 = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
            var y2 = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
            g2d.setColor(generateRandomColor());
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Draw random dots
        for (var i = 0; i < 300; i++) {
            var x = SECURE_RANDOM.nextInt(CAPTCHA_WIDTH);
            var y = SECURE_RANDOM.nextInt(CAPTCHA_HEIGHT);
            g2d.setColor(generateRandomColor());
            g2d.drawRect(x, y, 1, 1);
        }
    }

    private void applyShear(BufferedImage image) {
        var frames = 20;
        var period = SECURE_RANDOM.nextInt(10) + 5;
        var phase = SECURE_RANDOM.nextInt(2);
        var copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        var gCopy = copy.createGraphics();
        gCopy.drawImage(image, 0, 0, null);
        gCopy.dispose();

        for (var y = 0; y < CAPTCHA_HEIGHT; y++) {
            var displacement = (period >> 1) * Math.sin(
                    (double) y / period + (2 * Math.PI * phase) / frames);

            var dx = (int) displacement;
            if (dx >= 0) {
                image.setRGB(dx, y, CAPTCHA_WIDTH - dx, 1, copy.getRGB(0, y, CAPTCHA_WIDTH - dx, 1, null, 0, CAPTCHA_WIDTH), 0, CAPTCHA_WIDTH);
                image.setRGB(0, y, dx, 1, new int[dx], 0, dx);
            } else {
                var absDx = -dx;
                image.setRGB(0, y, CAPTCHA_WIDTH - absDx, 1, copy.getRGB(absDx, y, CAPTCHA_WIDTH - absDx, 1, null, 0, CAPTCHA_WIDTH), 0, CAPTCHA_WIDTH);
                image.setRGB(CAPTCHA_WIDTH - absDx, y, absDx, 1, new int[absDx], 0, absDx);
            }
        }
    }

    private String convertImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return BASE64_ENCODER.encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new BusinessException("error.auth.captcha.error");
        }
    }

    private Color generateRandomColor() {
        return new Color(
                SECURE_RANDOM.nextInt(200),
                SECURE_RANDOM.nextInt(200),
                SECURE_RANDOM.nextInt(200));
    }

    private Color generateLightColor() {
        return new Color(
                SECURE_RANDOM.nextInt(55) + 200,
                SECURE_RANDOM.nextInt(55) + 200,
                SECURE_RANDOM.nextInt(55) + 200);
    }
}
