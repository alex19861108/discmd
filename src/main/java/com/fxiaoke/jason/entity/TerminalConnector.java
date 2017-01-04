package com.fxiaoke.jason.entity;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by alex on 2016/10/21.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TerminalConnector {
  private String host;
  private Session session;
  private ChannelShell channel;
  private InputStream inputToChannel;
  private OutputStream outputStream;
  private PrintStream shellStream;
  private Thread thread;
}
