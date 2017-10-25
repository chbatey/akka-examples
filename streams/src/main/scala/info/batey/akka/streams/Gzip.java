package info.batey.akka.streams;

import akka.util.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class Gzip {
    public static void main(String[] args) {
        try {
            // Encode a String into bytes
            String inputString = "blahblahblah";
            byte[] input = inputString.getBytes("UTF-8");

            // Compress the bytes
            byte[] output = new byte[100];
            Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
//            compresser.setStrategy(Deflater.HUFFMAN_ONLY);
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();

            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(output, 0, compressedDataLength);
            byte[] result = new byte[100];
            int resultLength = decompresser.inflate(result);
            decompresser.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
            System.out.println("And it is back: " + outputString);
//
//            // Do it with a stream
//
//            byte[] buffer = new byte[1024];
//            Deflater empty = new Deflater(Deflater.BEST_SPEED);
//            empty.setStrategy(Deflater.HUFFMAN_ONLY);
//            int len = empty.deflate(buffer);
//            System.out.println("Length : " + len);
//            empty.finish();
//
//            byte[] emptyArray = new byte[0];
//            InputStream is = new ByteArrayInputStream(output, 0, 0);
//            GZIPInputStream gis = new GZIPInputStream(is);
//            gis.read();

        } catch (Exception ex) {
            // handle
        }
    }

}
