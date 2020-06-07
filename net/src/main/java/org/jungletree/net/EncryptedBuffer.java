package org.jungletree.net;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public class EncryptedBuffer {

    private final Cipher cipher;

    EncryptedBuffer(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
        cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
    }

    public ByteBuffer crypt(ByteBuffer in) {
        ByteBuffer out = ByteBuffer.allocate(in.capacity());
        try {
            cipher.update(in, out);
        } catch (ShortBufferException ex) {
            throw new RuntimeException("Encryption buffer was too short", ex);
        }
        return out;
    }
}
