package ru.duytsev.money.transfers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelWrapper {
    private boolean success = true;
    private Object data;

    public static ModelWrapper wrap(Object model) {
        ModelWrapper wrap = new ModelWrapper();
        wrap.setData(model);
        return wrap;
    }

    public static ModelWrapper wrapError(Error error) {
        return new ModelWrapper(false, error);
    }
}

