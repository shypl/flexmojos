/*
 * Flex-mojos is a set of maven plugins to allow maven users to compile, optimize, test and ... Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008 Marvin Herman Froeder
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package info.rvin.flexmojos.asdoc;

import info.rvin.flexmojos.utilities.MavenUtils;

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;

/**
 * Generates documentation for the <code>ActionScript code</code> in the project using the standard Asdoc tool
 * 
 * @author <a href="mailto:justin.edelson@mtvstaff.com">Justin Edelson</a>
 * @goal asdoc-report
 */
public class AsDocReport
    extends AsDocMojo
    implements MavenReport
{

    /**
     * Generates the site report
     * 
     * @component
     */
    private Renderer siteRenderer;

    /**
     * The name of the destination directory.
     * 
     * @parameter expression="${destDir}" default-value="asdoc"
     */
    private String destDir;

    /**
     * The name of the AsDoc report.
     * 
     * @parameter expression="${name}" default-value="ASDocs"
     */
    private String name;

    /**
     * The description of the AsDoc report.
     * 
     * @parameter expression="${description}" default-value="ASDoc API documentation."
     */
    private String description;

    /**
     * Specifies the destination directory where javadoc saves the generated HTML files.
     * 
     * @parameter expression="${project.reporting.outputDirectory}/asdoc"
     * @required
     */
    protected File reportOutputDirectory;

    public boolean canGenerateReport()
    {
        return true;
    }

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        getLog().info(
                       "Flex-mojos " + MavenUtils.getFlexMojosVersion( )
                           + " - GNU GPL License (NO WARRANTY) - See COPYRIGHT file" );

        try
        {
            SiteRendererSink sink = siteRenderer.createSink( getReportOutputDirectory(), getOutputName() + ".html" );

            generate( sink, Locale.getDefault() );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "An error has occurred in " + getName( Locale.ENGLISH )
                + " report generation:" + e.getMessage(), e );
        }
    }

    public void generate( Sink sink, Locale locale )
        throws MavenReportException
    {
        output = getReportOutputDirectory();
        try
        {
            // Not really sure why this class loader switching
            // is necessary. But it is.
            Thread currentThread = Thread.currentThread();
            ClassLoader savedCL = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader( getClass().getClassLoader() );
            super.execute();
            currentThread.setContextClassLoader( savedCL );
        }
        catch ( Exception e )
        {
            throw new MavenReportException( "Unable to generate report", e );
        }

    }

    public String getCategoryName()
    {
        return CATEGORY_PROJECT_REPORTS;
    }

    public String getDescription( Locale locale )
    {
        return description;
    }

    public String getName( Locale locale )
    {
        return name;
    }

    public String getOutputName()
    {
        return destDir + "/index";
    }

    public File getReportOutputDirectory()
    {
        if ( reportOutputDirectory == null )
        {
            return output;
        }

        return reportOutputDirectory;
    }

    public boolean isExternalReport()
    {
        return true;
    }

    public void setReportOutputDirectory( File outputDirectory )
    {
        if ( ( outputDirectory != null ) && ( !outputDirectory.getAbsolutePath().endsWith( destDir ) ) )
        {
            this.reportOutputDirectory = new File( outputDirectory, destDir );
        }
        else
        {
            this.reportOutputDirectory = outputDirectory;
        }

    }

}
