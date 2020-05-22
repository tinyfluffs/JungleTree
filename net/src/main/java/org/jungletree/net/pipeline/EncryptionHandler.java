package org.jungletree.net.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.List;

public class EncryptionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final EncryptedBuffer encodeBuf;
    private final EncryptedBuffer decodeBuf;

    public EncryptionHandler(SecretKey secretKey) throws GeneralSecurityException {
        this.encodeBuf = new EncryptedBuffer(Cipher.ENCRYPT_MODE, secretKey);
        this.decodeBuf = new EncryptedBuffer(Cipher.DECRYPT_MODE, secretKey);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        encodeBuf.crypt(msg, out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        decodeBuf.crypt(msg, out);
    }

    private static class EncryptedBuffer {
        private final Cipher cipher;

        private EncryptedBuffer(int mode, SecretKey sharedSecret) throws GeneralSecurityException {
            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(mode, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
        }

        public void crypt(ByteBuf msg, List<Object> out) {
            ByteBuffer outBuffer = ByteBuffer.allocate(msg.readableBytes());
            try {
                cipher.update(msg.nioBuffer(), outBuffer);
            } catch (ShortBufferException ex) {
                throw new RuntimeException("Encryption buffer was too short", ex);
            }
            outBuffer.flip();
            out.add(Unpooled.wrappedBuffer(outBuffer));
        }
    }
}
