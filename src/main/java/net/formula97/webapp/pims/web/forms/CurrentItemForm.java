/**
 *
 */
package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author f97one
 */
@Data
@NoArgsConstructor
public class CurrentItemForm {

    /**
     * マスタ種別
     */
    private String masterType;

    private Integer itemNameLength;

    private List<MasterItem> masterList;
}
