/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freetool.jstickerpages.pages;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author henrique
 */
public class StickerPage implements Printable{

    private LocalDate startDate;
    private int numberOfPages;
    private LocalDate currentDate;

    public StickerPage(LocalDate startDate, int numberOfPages) {
        this.startDate = startDate;
        this.numberOfPages = numberOfPages;
    }
    
    private void addXDate(int days) {
        for(int k = 0; k < days; k++)
            nextDate();
    }

    private void nextDate() {
        DayOfWeek dayOfWeek;

        do {
            currentDate = currentDate.plusDays(1);
            dayOfWeek = currentDate.getDayOfWeek();
        } while (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY);
    }

    private void previousDate() {
        DayOfWeek dayOfWeek;

        do {
            currentDate = currentDate.minusDays(1);
            dayOfWeek = currentDate.getDayOfWeek();
        } while (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY);
    }

    @Override
    public int print(Graphics grphcs, PageFormat pf, int i) throws PrinterException {
        if (i >= numberOfPages) {
            return NO_SUCH_PAGE;
        }
        
        currentDate = startDate.plusDays(0);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Graphics2D g2D = (Graphics2D) grphcs;
        g2D.translate(pf.getImageableX(), pf.getImageableY());

        Font headerFont = new Font("Serif", Font.BOLD, 18);
        Font textFont = new Font("Serif", Font.PLAIN, 12);

        int w = (int) pf.getImageableWidth(); // Valid page width; Inches * 72
        int h = (int) pf.getImageableHeight(); // Valid page height; Inches * 72

        // Draw outside box
        g2D.setPaint(Color.BLACK);
        g2D.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2D.drawRect(1, 1, w - 1, h - 1);

        // Define header size
        int headerHeight = g2D.getFontMetrics(headerFont).getHeight() + 30;

        int boxX, boxY;

        // Define sticker box sizes 
        double boxWAx = 2 + (1.0 / 4.0); // 2.25 of an inch
        double boxHAx = 1 + (9.0 / 16.0); // 1.5625 of an inch

        // Convert sticker box sizes to actual print page size
        double boxW = boxWAx * (72.0);
        double boxH = boxHAx * (72.0);
        double box2H = 2 * boxH;

        // Define minimal box gaps
        double baseVGap = (3.0 / 16.0) * (72.0); // 3/16 of a inch
        double baseHGap = (3.0 / 16.0) * (72.0); // 3/16 of a inch

        // Define number of Lines and columns
        int columns = (int) ((w - baseHGap) / (boxW + baseHGap));
        int lines = (int) (((h - headerHeight) - baseVGap) / (box2H + baseVGap));

        // define actual box gaps
        double ax = w - (columns * (boxW + baseHGap)) - baseHGap;
        double hGap = baseHGap + (ax / (columns + 1));

        ax = (h - headerHeight) - (lines * (box2H + baseVGap)) - baseVGap;
        double vGap = baseVGap + (ax / (lines + 1));
        
        // Correct date
        addXDate((columns * lines) * i);

        // Save begining date
        String header = currentDate.format(format);

        // Draw boxes
        BasicStroke boxStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke lineStroke = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke textStroke = new BasicStroke(0.5f);

        g2D.setFont(textFont);

        for (int l = 0; l < lines; l++) {
            for (int c = 0; c < columns; c++) {
                // Draw double box
                g2D.setStroke(boxStroke);
                g2D.drawRect((int) (hGap + (c * (boxW + hGap))), (headerHeight) + (int) (vGap + (l * (box2H + vGap))), (int) boxW, (int) box2H);
                
                // Draw box middle line
                g2D.setStroke(lineStroke);
                g2D.drawLine((int) (hGap + (c * (boxW + hGap))), (headerHeight) + (int) (vGap + (l * (box2H + vGap)) + boxH), (int) (hGap + (c * (boxW + hGap))) + (int) boxW, (headerHeight) + (int) (vGap + (l * (box2H + vGap)) + boxH));
                
                // Draw date to the boxes
                g2D.setStroke(textStroke);
                int toDrawHeight = g2D.getFontMetrics().getHeight() - g2D.getFontMetrics().getLeading();
                
                String toDraw = currentDate.format(format).concat(" - Entrada");
                int toDrawWidth = g2D.getFontMetrics().stringWidth(toDraw);
                g2D.drawString(toDraw, (int) (hGap + (c * (boxW + hGap))) + (int) (boxW / 2) - (toDrawWidth / 2), (headerHeight) + (int) (vGap + (l * (box2H + vGap)) + (boxH * 0.5)));

                toDraw = currentDate.format(format).concat(" - Saída");
                toDrawWidth = g2D.getFontMetrics().stringWidth(toDraw);
                g2D.drawString(toDraw, (int) (hGap + (c * (boxW + hGap))) + (int) (boxW / 2) - (toDrawWidth / 2), (headerHeight) + (int) (vGap + (l * (box2H + vGap)) + (boxH * 1.5)));

                nextDate();
            }
        }

        // Go back one day
        previousDate();

        // Draw title of page
        g2D.setFont(headerFont);

        header = header.concat(" - ").concat(currentDate.format(format));

        int headerWidth = g2D.getFontMetrics().stringWidth(header);

        g2D.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2D.drawString(header, (w / 2) - (headerWidth / 2), 20);

        // Define as a valid page
        return Printable.PAGE_EXISTS;
    }
}
