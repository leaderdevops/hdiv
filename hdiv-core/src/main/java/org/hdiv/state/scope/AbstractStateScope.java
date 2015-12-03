/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.state.scope;

import org.hdiv.context.RequestContext;
import org.hdiv.state.IState;

/**
 * Common code for {@link StateScope} implementation.
 * 
 * @since 2.1.7
 */
public abstract class AbstractStateScope implements StateScope {

	public String addState(RequestContext context, IState state, String token) {

		ScopedStateCache cache = this.getStateCache(context);
		if (cache == null) {
			cache = new ScopedStateCache();
		}

		String stateId = cache.addState(state, token);

		this.setStateCache(context, cache);

		return this.getScopePrefix() + "-" + stateId;
	}

	public IState restoreState(RequestContext context, int stateId) {

		ScopedStateCache cache = this.getStateCache(context);
		return cache == null ? null : cache.getState(stateId);
	}

	public String getStateToken(RequestContext context, int stateId) {

		ScopedStateCache cache = this.getStateCache(context);
		return cache == null ? null : cache.getStateToken(stateId);
	}

	public boolean isScopeState(String stateId) {

		int stateIndex = stateId.indexOf("-");
		return stateIndex > 0 && stateId.substring(0, stateIndex).equals(this.getScopePrefix());
	}

	protected abstract String getScopePrefix();

	protected abstract ScopedStateCache getStateCache(RequestContext context);

	protected abstract void setStateCache(RequestContext context, ScopedStateCache cache);

}
