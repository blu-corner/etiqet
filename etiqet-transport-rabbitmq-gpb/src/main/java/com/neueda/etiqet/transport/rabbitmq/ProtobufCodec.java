package com.neueda.etiqet.transport.rabbitmq;

import com.google.protobuf.Any;
import com.google.protobuf.Any.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.Codec;
import java.io.ByteArrayInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.impl.dv.util.HexBin;

/**
 * Codec wrapper for protocol buffers.
 */
public class ProtobufCodec implements Codec<Cdr, byte[]> {

  private static final Logger logger = LogManager.getLogger(ProtobufCodec.class);
  private ProtocolConfig protocolConfig;

  public ProtobufCodec() throws EtiqetException {
    protocolConfig = GlobalConfig.getInstance().getProtocol("fix");
  }


  @Override
  public byte[] encode(Cdr cdr) throws EtiqetException {
    try {
      Builder b = Any.newBuilder();
      new JsonFormat().merge(new ByteArrayInputStream(JsonUtils.cdrToJson(cdr).getBytes()), b);
      return b.build().toByteArray();
    } catch (Exception e) {
      logger.error(e);
      throw new SerializeException(e);
    }
  }

  @Override
  public Cdr decode(byte[] msg) throws EtiqetException {
    try {
      return JsonUtils.jsonToCdr(new JsonFormat().printToString(Any.parseFrom(msg)));
    } catch (InvalidProtocolBufferException e) {
      throw new EtiqetException("Could not decode message " + HexBin.encode(msg));
    }
  }
}
