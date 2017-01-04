package com.fxiaoke.jason.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alex on 2016/10/11.
 */
@Getter
@Setter
public class UserSchSessionsOutput extends ConcurrentHashMap<Integer, SessionOutput> {
  //instance id, host output
}
