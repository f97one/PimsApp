/**
 *
 */
package net.formula97.webapp.pims.web.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author f97one
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterItem {

    private Integer itemId;

    private String itemName;

    private Integer displayOrder;

    private Boolean treatAsFinished;
}
