package com.fxiaoke.jason.entity;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by alex on 2016/10/10.
 */
@Getter
@Setter
@NoArgsConstructor
public class SchSession {
  Long userId;
  Session session;
  Channel channel;
  PrintStream commander;
  InputStream outFromChannel;
  OutputStream inputToChannel;
  Machine machine;
}
