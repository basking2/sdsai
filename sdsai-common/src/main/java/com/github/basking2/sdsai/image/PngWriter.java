/**
 * Copyright (c) 2017-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Utility class for writing a {@code .png} with some meta data.
 */
public class PngWriter {

    /**
     * Write a {@link BufferedImage} to the {@link OutputStream}.
     * @param out The output stream.
     * @param image The image to write to the output stream.
     * @param tEXt Strings to enter into the tEXt fields of the PNG. Well known values for the key value are
     *             Title, Author, Description, Copyright, Creation Time, Software, Disclaimer, Warning,
     *             Source, or Comment.
     *
     * @throws IOException On writing errors.
     */
    public static void writeImageToByteArray(
            final OutputStream out,
            final BufferedImage image,
            final Map<String, String> tEXt
    ) throws IOException
    {
        // zTXt is zipped tEXt
        final IIOMetadataNode tEXtComment = new IIOMetadataNode("tEXt");

        // Append all text metadata entries to tEXtComment.
        for (final Map.Entry<String, String> entry : tEXt.entrySet()) {
            final IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
            textEntry.setAttribute("keyword", entry.getKey());
            textEntry.setAttribute("value", entry.getValue());
            tEXtComment.appendChild(textEntry);
        }

        // Build an ImageWriter to use.
        final ImageWriter writer = ImageIO.getImageWritersBySuffix("png").next();

        // Get and adjust the default meta data for the image to have our tEXtComment parts.
        final IIOImage iIOImage = setMetadataRoot(tEXtComment, writer, image);

        // Write the image.
        writeImage(out, writer, iIOImage);
    }

    /**
     * Merge the metadata tree with the given {@link IIOMetadataNode}.
     *
     * @param tEXtComment The text fields to merge.
     * @param writer The writer that defines the default metadata tree we merge against.
     * @param image The image that defines the type of image.
     * @return The default metadata object merged with {@code tEXtComment}.
     * @throws IIOInvalidTreeException On error.
     */
    private static IIOImage setMetadataRoot(final IIOMetadataNode tEXtComment, final ImageWriter writer, final BufferedImage image) throws IIOInvalidTreeException {
        final String METADATA_FORMAT = "javax_imageio_png_1.0";

        final IIOMetadataNode metadataRoot = new IIOMetadataNode(METADATA_FORMAT);

        metadataRoot.appendChild(tEXtComment);

        final IIOMetadata defaultMetadata = writer.getDefaultImageMetadata(
            ImageTypeSpecifier.createFromBufferedImageType(image.getType()),
            writer.getDefaultWriteParam());

        defaultMetadata.mergeTree(METADATA_FORMAT, metadataRoot);

        final IIOImage iioImage = new IIOImage(image, null, null);

        iioImage.setMetadata(defaultMetadata);

        return iioImage;
    }

    /**
     * Write the given image with the given metadata to the given output stream.
     * @param out The output stream.
     * @param writer The writer to encode the image by.
     * @param iioImage The image data, metadata and thumbnail, if any, to write.
     * @throws IOException On errors.
     */
    private static void writeImage(final OutputStream out, final ImageWriter writer, final IIOImage iioImage) throws IOException {
        // Write the image.
        final MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(out);
        writer.setOutput(mcios);
        writer.write(iioImage);
        mcios.close();
    }
}
