package com.markit.api;

import com.markit.api.handlers.WatermarkHandler;
import com.markit.api.handlers.WatermarksHandler;
import com.markit.exceptions.UnsupportedFileTypeException;
import com.markit.exceptions.WatermarkingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Oleg Cheban
 * @since 1.0
 */
public class WatermarkServiceImpl implements WatermarkService.File, WatermarkService.Watermark {
    private static final Log logger = LogFactory.getLog(WatermarkServiceImpl.class);
    private FileType fileType;
    private WatermarkHandler watermarkHandler;
    private final List<WatermarkAttributes> watermarks = new ArrayList<>();
    private WatermarkAttributes currentWatermark;
    private Executor executor;

    public WatermarkServiceImpl() {
    }

    public WatermarkServiceImpl(Executor e) {
        this.executor = e;
    }

    @Override
    public WatermarkService.Watermark watermark(PDDocument document) {
        return configureDefaultParams(FileType.PDF, new WatermarksHandler().getHandler(document, FileType.PDF, this.executor));
    }

    @Override
    public WatermarkService.Watermark watermark(byte[] fileBytes, FileType ft) {
        return configureDefaultParams(ft, new WatermarksHandler().getHandler(fileBytes, ft, this.executor));
    }

    @Override
    public WatermarkService.Watermark watermark(File file, FileType ft) {
        return configureDefaultParams(ft, new WatermarksHandler().getHandler(file, ft, this.executor));
    }

    private WatermarkService.Watermark configureDefaultParams(FileType ft, WatermarkHandler h) {
        currentWatermark = new WatermarkAttributes();
        currentWatermark.setMethod(defineMethodByFileType(ft));
        this.fileType = ft;
        this.watermarkHandler = h;
        return this;
    }

    @Override
    public WatermarkService.Watermark withText(String text) {
        currentWatermark.setText(text);
        return this;
    }

    @Override
    public WatermarkService.Watermark ofSize(int size) {
        currentWatermark.setTextSize(size);
        return this;
    }

    @Override
    public WatermarkService.Watermark usingMethod(WatermarkMethod method) {
        currentWatermark.setMethod(method);
        return this;
    }

    @Override
    public WatermarkService.Watermark atPosition(WatermarkPosition position) {
        currentWatermark.setPosition(position);
        return this;
    }

    @Override
    public WatermarkService.Watermark inColor(Color c) {
        currentWatermark.setColor(c);
        return this;
    }

    @Override
    public WatermarkService.Watermark withOpacity(float opacity) {
        currentWatermark.setOpacity(opacity);
        return this;
    }

    @Override
    public WatermarkService.Watermark withDpi(float d) {
        currentWatermark.setDpi(d);
        return this;
    }

    @Override
    public WatermarkService.Watermark withTrademark() {
        currentWatermark.setTrademark(true);
        return this;
    }

    @Override
    public WatermarkService.Watermark rotate(int degree) {
        currentWatermark.setRotation(degree);
        return this;
    }

    @Override
    public WatermarkService.Watermark and() {
        if (!currentWatermark.getText().isEmpty()) {
            watermarks.add(currentWatermark);
            currentWatermark = new WatermarkAttributes();
            currentWatermark.setMethod(defineMethodByFileType(fileType));
        }
        return this;
    }

    @Override
    public byte[] apply() {
        try {
            and();
            return this.watermarkHandler.apply(this.watermarks);
        } catch (IOException e) {
            logger.error("Failed to watermark file", e);
            throw new WatermarkingException("Error watermarking the file", e);
        }
    }

    private WatermarkMethod defineMethodByFileType(FileType ft){
        switch (ft){
            case JPEG:
            case PNG:
            case TIFF:
            case BMP: return WatermarkMethod.DRAW;
            case PDF: return WatermarkMethod.OVERLAY;
            default: throw new UnsupportedFileTypeException("Unsupported file type: " + ft);
        }
    }
}
