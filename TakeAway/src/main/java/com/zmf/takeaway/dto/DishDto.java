package com.zmf.takeaway.dto;

import com.zmf.takeaway.entity.Dish;
import com.zmf.takeaway.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
