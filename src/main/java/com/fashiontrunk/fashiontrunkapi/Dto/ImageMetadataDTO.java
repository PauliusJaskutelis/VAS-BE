package com.fashiontrunk.fashiontrunkapi.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageMetadataDTO {
    private String filename;
    private Integer width;
    private Integer height;
    @JsonProperty("color_mode")
    private String colorMode;
    private String format;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
