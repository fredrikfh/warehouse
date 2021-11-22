package ui;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import zxing.BarcodeFormat;
import zxing.MultiFormatWriter;
import zxing.common.BitMatrix;


/**
 * Wrapper class for a static function that generates an InputStream with a
 * barcode image from a barcode string.
 */
public interface BarcodeCreator {
  public static Image generateBarcodeImage(String barcodeText) throws Exception {
    MultiFormatWriter writer = new MultiFormatWriter();

    BitMatrix bm = writer.encode(barcodeText, BarcodeFormat.EAN_13, 200, 60);
    WritableImage image = new WritableImage(200, 60);

    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        image.getPixelWriter().setColor(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
      }
    }
    
    return image;
  }
}
