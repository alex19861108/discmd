package com.fxiaoke.jason.service;

import com.fxiaoke.jason.entity.Machine;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by alex on 16/8/31.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class MachineLabelMapServiceTest {

  @Resource
  private MachineLabelMapService machineLabelMapService;

  @Test
  public void testFindMachinesForLabel() {
    List<Machine> machines = machineLabelMapService.findMachinesForLabel(Long.valueOf(1));

    Assert.assertTrue(1 < machines.size());
  }
}
