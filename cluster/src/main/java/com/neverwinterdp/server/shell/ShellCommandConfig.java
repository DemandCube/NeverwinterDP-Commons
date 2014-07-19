package com.neverwinterdp.server.shell;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@interface ShellCommandConfig {
  String name();
}