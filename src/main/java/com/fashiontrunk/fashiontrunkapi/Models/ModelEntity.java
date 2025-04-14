package com.fashiontrunk.fashiontrunkapi.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "models")
public class ModelEntity {
    @Id
    private UUID id;

    private String filename;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "input_height")
    private int inputHeight;

    @Column(name = "input_width")
    private int inputWidth;

    @Column(name = "color_mode")
    private String colorMode;

    private String preprocessing;

    @Column(name = "input_shape", columnDefinition = "TEXT")
    private String inputShape;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "status")
    private String status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public int getInputHeight() {
        return inputHeight;
    }

    public void setInputHeight(int inputHeight) {
        this.inputHeight = inputHeight;
    }

    public int getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(int inputWidth) {
        this.inputWidth = inputWidth;
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

    public String getPreprocessing() {
        return preprocessing;
    }

    public void setPreprocessing(String preprocessing) {
        this.preprocessing = preprocessing;
    }

    public String getInputShape() {
        return inputShape;
    }

    public void setInputShape(String inputShape) {
        this.inputShape = inputShape;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
