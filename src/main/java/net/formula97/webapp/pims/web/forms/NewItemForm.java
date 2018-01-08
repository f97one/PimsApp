package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewItemForm {

    /**
     * マスタ種別
     */
    private String masterType;

    /**
     * マスタ名称
     */
    private String itemName;

    /**
     * マスタ名称の最大長
     */
    private int itemNameLength;

    /**
     * 終了とみなすフラグ
     */
    private boolean treatAsFinished;
}
