package com.lianshang.generator.commons;

public enum GenerateFileTypeEnum {

  MAPPER_INTERFACE(1, "mapper.java"),
  ENTITY(2, "entity.java"),
  DTO(3, "dto.java"),
  MAPPER_XML(4, "mapper.xml"),
  SERVICE(5, "service.java"),
  SERVICE_IMPL(6, "serviceImpl.java"),
  CONTROLLER(7, "controller.java"),
  EXAMPLE(8, "example.java"),
  ;

  private Integer code;

  private String name;

  GenerateFileTypeEnum(Integer code , String name) {
    this.code = code;
    this.name = name;
  }

  public static GenerateFileTypeEnum getByCode(Integer code) {
    for (GenerateFileTypeEnum _enum : values()) {
      if (_enum.code.equals(code)) {
        return _enum;
      }
    }
    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

}
