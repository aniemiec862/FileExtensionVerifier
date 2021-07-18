package ExtensionVerifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExtensionFromFileGetter implements IFileExtensionGetter{
    @Override
    public String getExtension(String filePath, String extensionFromPath) throws IOException {
        //because txt files do not have their magic numbers, I had to find an another way to verify them
        if (extensionFromPath.equals("txt"))
            return verifyTextExtension(filePath);

        String header = getHeader(filePath);

        if (header != null && header.length() > 0) {
            header = header.toUpperCase();
            Extension[] fileTypes = Extension.values();

            for (Extension type : fileTypes) {
                if (header.startsWith(type.getValue())) {
                    return type.toString();
                }
            }
        }
        return "other";
    }

    private String verifyTextExtension(String filePath) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);
        String mimeType = Files.probeContentType(path);

        if (!mimeType.equals("text/plain"))
            throw new IllegalArgumentException("Invalid extension provided");

        return Extension.TXT.toString();
    }

    private String getHeader(String filePath) {
        byte[] bytes = new byte[28];

        try (InputStream inputStream = new FileInputStream(filePath)) {
            inputStream.read(bytes, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return changeToHex(bytes);
    }

    public static String changeToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes == null || bytes.length <= 0)
            return null;

        for (byte b : bytes) {
            int value = b & 0xFF;
            String hexString = Integer.toHexString(value);
            if (hexString.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hexString);
        }
        return stringBuilder.toString();
    }
}