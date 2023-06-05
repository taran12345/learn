// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * HomeController for swagger. This is to access the main swagger page by using
 * "/" without having to know the html page name (swagger-ui.html).
 *
 * @author glenh
 *
 */
@Controller
public class SwaggerController {

  /**
   * home.
   *
   * @return String
   */
  @RequestMapping("/")
  public ModelAndView home() {

    return new ModelAndView("redirect:/swagger-ui.html");
  }
}
