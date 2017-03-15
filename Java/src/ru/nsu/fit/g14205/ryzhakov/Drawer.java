package ru.nsu.fit.g14205.ryzhakov;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Drawer {
    private Graphics2D g2d;
    private BufferedImage image;

    private int lineWidth = 1;
    private Color color = Color.BLACK;
    public static final int FONT_SIZE = 12;

    public Drawer(Graphics2D g2d, BufferedImage image) {
        this.g2d = g2d;
        this.image = image;

        g2d.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
    }

    public void setLineWidth(int width){
        lineWidth = width;

        g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    private void drawManualLine(int x1, int y1, int x2, int y2){
        boolean top_orient = abs(y2 - y1) > abs(x2 - x1);

        if (top_orient){
            int temp;

            temp = x1;
            x1 = y1;
            y1 = temp;

            temp = x2;
            x2 = y2;
            y2 = temp;
        }

        if (x1 > x2){
            int temp;

            temp = x1;
            x1 = x2;
            x2 = temp;

            temp = y1;
            y1 = y2;
            y2 = temp;
        }

        int dx = abs(x2 - x1);
        int dy = abs(y2 - y1);

        int error = dx / 2;

        int yStep = (y1 < y2) ? 1 : -1;

        int y = y1;
        for (int x = min(x1, x2); x <= max(x1,x2); ++x)
        {
            image.setRGB((top_orient ? y : x), (top_orient ? x : y), color.getRGB());

            error -= dy;
            if (error < 0)
            {
                y += yStep;
                error += dx;
            }
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2){
        if(lineWidth != 1){
            g2d.drawLine(x1, y1, x2, y2);
            return;
        }

        drawManualLine(x1, y1, x2, y2);
    }

    public void setColor(Color color){
        this.color = color;
        g2d.setColor(color);
    }

    private class Span{
        int y = 0;
        int x1 = 0;
        int x2 = 0;

        Span(int y, int x1, int x2) {
            this.y = y;
            this.x1 = min(x1, x2);
            this.x2 = max(x1, x2);
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Span)){
                return false;
            }

            Span so = (Span)o;
            return so.y == y && so.x1 == x1 && so.x2 == x2;
        }
    }

    private Span findSpan(int x, int y, int color){
        int left = x;
        int right = x;

        while(left - 1 >= 0 && image.getRGB(left - 1, y) == color){
            --left;
        }

        while(right + 1 < image.getWidth() && image.getRGB(right + 1, y) == color){
            ++right;
        }

        return new Span(y, left, right);
    }

    private void paintSpan(Span span){
        drawManualLine(span.x1, span.y, span.x2, span.y);
    }

    private void runOnSpan(Stack<Span> spans, Span currentSpan, int oldColor, int direction){
        if(currentSpan.y - direction > 0 && currentSpan.y - direction < image.getHeight()) {
            for (int ix = currentSpan.x1; ix <= currentSpan.x2; ++ix) {
                if (image.getRGB(ix, currentSpan.y - direction) == oldColor) {
                    Span newSpan = findSpan(ix, currentSpan.y - direction, oldColor);

                    if (!spans.contains(newSpan)) {
                        spans.push(newSpan);
                    }

                    ix = newSpan.x2;
                }
            }
        }
    }

    public void paintArea(int x, int y){
        int oldColor = image.getRGB(x, y);

        Stack<Span> spans = new Stack<>();

        Span startSpan = findSpan(x, y, oldColor);
        spans.push(startSpan);

        for(int i = 0; i < spans.size(); ++i){
            paintSpan(spans.get(i));
            runOnSpan(spans, spans.get(i), oldColor, -1);
            runOnSpan(spans, spans.get(i), oldColor, 1);
        }
    }

    public void drawText(String text, int x, int y){
        g2d.drawString(text, x, y);
    }
}
