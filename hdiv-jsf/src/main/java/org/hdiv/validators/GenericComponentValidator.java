/**
 * Copyright 2005-2016 hdiv.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.validators;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.hdiv.util.UtilsJsf;
import org.hdiv.validation.ValidationContext;

public class GenericComponentValidator extends AbstractComponentValidator {

	private static String ALL = "@all";

	private static String FORM = "@form";

	private static String THIS = "@this";

	private static String NONE = "@none";

	public GenericComponentValidator() {
		super(UIComponent.class);
	}

	public void validate(final ValidationContext context, final UIComponent component) {

		if (component instanceof ClientBehaviorHolder) {
			validateClientBehaviors(context, (ClientBehaviorHolder) component);
		}

		String clientId = null;

		if (component instanceof UIParameter) {
			UIParameter parameter = (UIParameter) component;
			clientId = parameter.getName();
		}
		else {
			clientId = UtilsJsf.removeRowId(component.getClientId(context.getFacesContext()));
		}

		context.acceptParameter(clientId, clientId);
	}

	protected void validateClientBehaviors(final ValidationContext context, final ClientBehaviorHolder component) {

		Map<String, List<ClientBehavior>> clientBehaviors = component.getClientBehaviors();
		if (!clientBehaviors.isEmpty()) {
			for (String event : clientBehaviors.keySet()) {
				List<ClientBehavior> behaviors = clientBehaviors.get(event);
				for (ClientBehavior behavior : behaviors) {
					validateClientBehavior(context, component, event, behavior);
				}
			}
		}
	}

	protected void validateClientBehavior(final ValidationContext context, final ClientBehaviorHolder component,
			final String behaviourEvent, final ClientBehavior behavior) {
		if (behavior instanceof AjaxBehavior) {
			AjaxBehavior ajaxBehavior = (AjaxBehavior) behavior;
			Collection<String> executeIds = ajaxBehavior.getExecute();
			Collection<String> renderIds = ajaxBehavior.getRender();

			context.acceptParameter("javax.faces.partial.ajax", "true");
			// TODO If component is child of UIData the 'javax.faces.source' will contain an indexed id: 'form:list:1:button'
			context.acceptParameter("javax.faces.source", ((UIComponent) component).getClientId());
			context.acceptParameter("javax.faces.behavior.event", behaviourEvent);
			context.acceptParameter("javax.faces.partial.execute", resolveClientIds((UIComponent) component, executeIds));
			context.acceptParameter("javax.faces.partial.render", resolveClientIds((UIComponent) component, renderIds));
			context.acceptParameter("javax.faces.partial.event", "click");// TODO more valid values??
		}
	}

	protected String resolveClientIds(final UIComponent component, final Collection<String> ids) {

		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String id : ids) {
			if (i > 0) {
				sb.append(' ');
			}
			i++;
			if (id.charAt(0) == '@') {
				if (id.equals(FORM)) {
					sb.append(UtilsJsf.findParentForm(component).getClientId());
				}
				else if (id.equals(THIS)) {
					sb.append(component.getClientId());
				}
				// TODO @none and @all
			}
			else if (id.charAt(0) == ':') {
				sb.append(id.substring(1));
			}
			else {
				sb.append(id);
			}
		}
		return sb.toString();
	}

}
