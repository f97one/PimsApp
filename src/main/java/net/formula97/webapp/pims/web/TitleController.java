/**
 * 
 */
package net.formula97.webapp.pims.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("")
public class TitleController {

	@RequestMapping(method = RequestMethod.GET)
	public String createTitle() {
		// TODO: タイトル画面を入れる処理を書く
		
		return "title";
	}
}
