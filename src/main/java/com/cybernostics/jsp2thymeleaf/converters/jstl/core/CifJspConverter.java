/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter;

/**
 *
 * @author jason
 */
public class CifJspConverter extends JspTagElementConverter
{

    public CifJspConverter()
    {
        super("if", "block");
        renamesAttribute("test", "if", thymeleafNS);

    }

}
