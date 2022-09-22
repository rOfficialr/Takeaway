package com.zmf.takeaway.dto;

import com.zmf.takeaway.entity.Setmeal;
import com.zmf.takeaway.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
