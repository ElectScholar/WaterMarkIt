package com.markit.pdf.overlay;

import com.markit.api.WatermarkAttributes;
import com.markit.api.WatermarkingMethod;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * An interface for adding watermarks to a PDF page. ({@link WatermarkingMethod#OVERLAY method}
 *
 * @author Oleg Cheban
 * @since 1.0
 */
public interface OverlayPdfWatermarker {
    /**
     * Overlay a text watermark to a specific page of a PDF document.
     *
     * @param document The PDF document to which the watermark will be applied.
     * @param pageIndex The index of the page to be watermarked (zero-based).
     * @param attrs The attributes of watermark
     * @param font The font of watermark
     */
    void watermark(PDDocument document, int pageIndex, List<WatermarkAttributes> attrs, Optional<PDType0Font> font) throws IOException;
}
