/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
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

package org.torquebox.injection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.torquebox.common.util.StringUtils;
import org.torquebox.interp.core.RubyComponentResolver;
import org.torquebox.mc.AttachmentUtils;

public abstract class AbstractRubyComponentDeployer extends AbstractDeployer {

    public AbstractRubyComponentDeployer() {
    }

    public void setInjectionAnalyzer(InjectionAnalyzer injectionAnalyzer) {
        this.injectionAnalyzer = injectionAnalyzer;
    }

    public InjectionAnalyzer getInjectionAnalyzer() {
        return this.injectionAnalyzer;
    }

    protected BeanMetaData createComponentResolver(DeploymentUnit unit, String componentName, String rubyClassName, Map<String, Object> initParams)
            throws DeploymentException {
        String beanName = AttachmentUtils.beanName( unit, RubyComponentResolver.class, "" + this.counter.getAndIncrement() );
        BeanMetaDataBuilder resolverBuilder = BeanMetaDataBuilder.createBuilder( beanName, RubyComponentResolver.class.getName() );

        resolverBuilder.addPropertyMetaData( "rubyClassName", StringUtils.camelize( rubyClassName ) );
        resolverBuilder.addPropertyMetaData( "rubyRequirePath", StringUtils.underscore( rubyClassName ) );
        resolverBuilder.addPropertyMetaData( "initializeParamsMap", initParams );
        resolverBuilder.addPropertyMetaData( "componentName", componentName );

        setUpInjections( unit, resolverBuilder, rubyClassName );

        AttachmentUtils.attach( unit, resolverBuilder.getBeanMetaData() );

        return resolverBuilder.getBeanMetaData();
    }

    public void setUpInjections(DeploymentUnit unit, BeanMetaDataBuilder beanBuilder, String rubyClassName) throws DeploymentException {
        try {
            RubyProxyInjectionBuilder injectionBuilder = new RubyProxyInjectionBuilder( unit, this.injectionAnalyzer, beanBuilder );
            injectionBuilder.build( StringUtils.underscore( rubyClassName ) + ".rb" );
        } catch (InjectionException e) {
            throw new DeploymentException( e );
        } catch (IOException e) {
            throw new DeploymentException( e );
        } catch (URISyntaxException e) {
            throw new DeploymentException( e );
        }
    }

    private AtomicInteger counter = new AtomicInteger();
    private InjectionAnalyzer injectionAnalyzer;

}
