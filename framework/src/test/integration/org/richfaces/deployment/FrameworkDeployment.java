/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.richfaces.deployment;

import java.io.File;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.richfaces.cache.Cache;
import org.richfaces.configuration.ConfigurationService;
import org.richfaces.context.AjaxDataSerializer;
import org.richfaces.el.GenericsIntrospectionService;
import org.richfaces.javascript.JavaScriptService;
import org.richfaces.l10n.MessageFactory;
import org.richfaces.push.PushContextFactory;
import org.richfaces.resource.ResourceCodec;
import org.richfaces.resource.ResourceLibraryFactory;
import org.richfaces.resource.external.ExternalResourceTracker;
import org.richfaces.resource.external.ExternalStaticResourceFactory;
import org.richfaces.services.DependencyInjector;
import org.richfaces.services.Uptime;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;
import org.richfaces.skin.SkinFactory;
import org.richfaces.wait.Condition;
import org.richfaces.wait.Wait;
import org.richfaces.wait.WaitTimeoutException;

public class FrameworkDeployment extends Deployment {

    public FrameworkDeployment(Class<?> testClass) {
        super(testClass);

        withWholeFramework();
        withArquillianExtensions();
        withWaiting();

        // prevents scanning of inner classes
        archive().addAsWebInfResource(new File("src/test/resources/beans.xml"));
    }

    public void withWholeFramework() {
        File richfacesJar = new File("target/richfaces.jar");
        if (!richfacesJar.exists()) {
            throw new IllegalStateException("The built library '" + richfacesJar.getPath()
                    + "' doesn't exist; be sure to run 'mvn package' before running framework tests");
        }
        archive().addAsLibrary(richfacesJar);
    }

    /**
     * Adds {@link CoreTestingRemoteExtension} (for testing Core with Arquillian) and its dependencies
     * @return
     */
    private void withArquillianExtensions() {
        archive().addAsServiceProvider(RemoteLoadableExtension.class, CoreTestingRemoteExtension.class);
        archive().addClasses(CoreTestingRemoteExtension.class, CoreServicesEnricher.class);
        archive().addClasses(ConfigurationService.class, SkinFactory.class, AjaxDataSerializer.class,
                ResourceCodec.class, Cache.class, Uptime.class, DependencyInjector.class, MessageFactory.class,
                ResourceLibraryFactory.class, PushContextFactory.class, JavaScriptService.class,
                GenericsIntrospectionService.class, ExternalResourceTracker.class, ExternalStaticResourceFactory.class);
    }

    private void withWaiting() {
        archive().addClasses(Condition.class, Wait.class, WaitTimeoutException.class);
    }

    public FaceletAsset baseFacelet(String name) {
        FaceletAsset p = new FaceletAsset();

        this.archive().add(p, name);

        return p;
    }
}