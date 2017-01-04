package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alex on 2016/10/10.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserSchSessions extends ConcurrentHashMap<Integer, SchSession>{
  // sessionId -> SchSession
}
