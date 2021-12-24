package net.mythonia.carepackage.entity.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Prize {

    private final String prizeName;
    private final String cmd;
    private final int chance;

}