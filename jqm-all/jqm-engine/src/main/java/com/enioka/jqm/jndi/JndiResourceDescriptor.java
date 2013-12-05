/**
 * Copyright © 2013 enioka. All rights reserved
 * Authors: Marc-Antoine GOUILLART (marc-antoine.gouillart@enioka.com)
 *          Pierre COPPEE (pierre.coppee@enioka.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.enioka.jqm.jndi;

import javax.naming.Reference;
import javax.naming.StringRefAddr;

class JndiResourceDescriptor extends Reference
{
	private static final long serialVersionUID = 3348684996519775949L;

	JndiResourceDescriptor(String resourceClass, String description, String scope, String auth, boolean singleton, String factory,
			String factoryLocation)
			{
		super(resourceClass, factory, factoryLocation);
		StringRefAddr refAddr = null;
		if (description != null)
		{
			refAddr = new StringRefAddr("description", description);
			add(refAddr);
		}
		if (scope != null)
		{
			refAddr = new StringRefAddr("scope", scope);
			add(refAddr);
		}
		if (auth != null)
		{
			refAddr = new StringRefAddr("auth", auth);
			add(refAddr);
		}
		// singleton is a boolean so slightly different handling
		refAddr = new StringRefAddr("singleton", Boolean.toString(singleton));
		add(refAddr);
			}
}