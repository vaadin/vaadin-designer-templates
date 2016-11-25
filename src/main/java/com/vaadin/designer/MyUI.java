package com.vaadin.designer;

import java.util.Arrays;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("designertemplate")
public class MyUI extends UI {

	private final static String[] views = { "DashboardTemplateDark", "DashboardTemplateDark", "DashboardTemplateBlue",
			"DashboardTemplateVivid", "FormTemplateDark", "FormTemplateBlue", "FormTemplateVivid",
			"NavigationTemplateDark", "NavigationTemplateBlue", "NavigationTemplateVivid", "ResponsiveApplication" };

	private final static String PKG = "com.vaadin.designer.designertemplate.";

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		new Navigator(this, this);
		getNavigator().setErrorView(TemplateListingView.class);
		// if (getNavigator().getState().isEmpty()) {
		//
		// }

		Arrays.asList(views).forEach(view -> getNavigator().addView(view, MyViewChangerView.class));
	}

	public static class MyViewChangerView extends VerticalLayout implements View {

		@Override
		public void enter(ViewChangeEvent event) {
			try {
				UI.getCurrent().setContent((Component) Class.forName(PKG + event.getViewName()).newInstance());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static class TemplateListingView extends VerticalLayout implements View {

		public TemplateListingView() {
			setMargin(true);
		}

		@Override
		public void enter(ViewChangeEvent event) {
			ListSelect list = new ListSelect();
			addComponent(list);
			Arrays.asList(views).forEach(list::addItem);
			list.addValueChangeListener(valueChange -> UI.getCurrent().getNavigator()
					.navigateTo((String) valueChange.getProperty().getValue()));
		}

	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
