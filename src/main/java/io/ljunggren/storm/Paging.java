package io.ljunggren.storm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Paging {

    private int page;
    private int rows;

}
