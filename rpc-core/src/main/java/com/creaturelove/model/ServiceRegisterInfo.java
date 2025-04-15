package com.creaturelove.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo<T> {
    // service name
    private String serviceName;

    // implementation class
    private Class<? extends T> implClass;
}
