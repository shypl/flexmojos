/**
 * Flexmojos is a set of maven goals to allow maven users to compile, optimize and test Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008-2012  Marvin Froeder <marvin@flexmojos.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sonatype.flexmojos.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * this class provides functions used to generate a relative path from two absolute paths
 * 
 * @author Marvin H. Froeder
 * @author David M. Howard
 */
public class PathUtil
{
    /**
     * break a path down into individual elements and add to a list. example : if a path is /a/b/c/d.txt, the breakdown
     * will be [d.txt,c,b,a]
     * 
     * @param f input file
     * @return a List collection with the individual elements of the path in reverse order
     */
    private static List<String> getPathList( File f )
    {
        List<String> l = new ArrayList<String>();
        try
        {
            File r = f.getCanonicalFile();
            while ( r != null )
            {
                l.add( r.getName() );
                r = r.getParentFile();
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            l = null;
        }
        return l;
    }

    /**
     * figure out a string representing the relative path of 'f' with respect to 'r'
     * 
     * @param r home path
     * @param f path of file
     */
    private static String matchPathLists( List<String> r, List<String> f )
    {
        int i;
        int j;
        String s;
        // start at the beginning of the lists
        // iterate while both lists are equal
        s = "";
        i = r.size() - 1;
        j = f.size() - 1;

        // first eliminate common root
        while ( ( i >= 0 ) && ( j >= 0 ) && ( r.get( i ).equals( f.get( j ) ) ) )
        {
            i--;
            j--;
        }

        // for each remaining level in the home path, add a ..
        for ( ; i >= 0; i-- )
        {
            s += ".." + File.separator;
        }

        // for each level in the file path, add the path
        for ( ; j >= 1; j-- )
        {
            s += f.get( j ) + File.separator;
        }

        // file name
        s += f.get( j );
        return s;
    }

    /**
     * get relative path of File 'f' with respect to 'home' directory example : home = /a/b/c f = /a/d/e/x.txt s =
     * getRelativePath(home,f) = ../../d/e/x.txt
     * 
     * @param home base path, should be a directory, not a file, or it doesn't make sense
     * @param f file to generate path for
     * @return path from home to f as a string
     */
    public static String getRelativePath( File home, File f )
    {
        List<String> homelist = getPathList( home );
        List<String> filelist = getPathList( f );
        return matchPathLists( homelist, filelist );
    }

    /**
     * test the function
     */
    public static void main( String args[] )
    {
        if ( args.length != 2 )
        {
            System.out.println( "RelativePath <home> <file>" );
            return;
        }
        System.out.println( "home = " + args[0] );
        System.out.println( "file = " + args[1] );
        System.out.println( "path = " + getRelativePath( new File( args[0] ), new File( args[1] ) ) );
    }

    public static String getCanonicalPath( File file )
    {
        if ( file == null )
        {
            return null;
        }

        try
        {
            return file.getCanonicalPath();
        }
        catch ( IOException e )
        {
            return file.getAbsolutePath();
        }
    }

    public static List<String> getCanonicalPathsList( File[] files )
    {
        if ( files == null )
        {
            return null;
        }
        return Arrays.asList( getCanonicalPaths( files ) );
    }

    public static String[] getCanonicalPaths( File... files )
    {
        if ( files == null )
        {
            return null;
        }

        String[] paths = new String[files.length];
        for ( int i = 0; i < paths.length; i++ )
        {
            paths[i] = getCanonicalPath( files[i] );
        }
        return paths;
    }

    public static String getCanonicalPathString( File[] files )
    {
        if ( files == null )
        {
            return null;
        }

        StringBuilder paths = new StringBuilder();
        for ( File file : files )
        {
            if ( paths.length() != 0 )
            {
                paths.append( File.pathSeparatorChar );
            }
            paths.append( getCanonicalPath( file ) );
        }
        return paths.toString();
    }

    public static String[] getCanonicalPaths( Collection<File> files )
    {
        if ( files == null )
        {
            return null;
        }

        return getCanonicalPaths( files.toArray( new File[files.size()] ) );
    }

    public static List<File> getFilesList( Collection<String> paths )
    {
        if ( paths == null )
        {
            return null;
        }

        return Arrays.asList( getFiles( paths ) );
    }

    public static File[] getFiles( String... paths )
    {
        if ( paths == null )
        {
            return null;
        }

        return getFiles( Arrays.asList( paths ) );
    }

    public static File[] getFiles( Collection<String> paths )
    {
        if ( paths == null )
        {
            return null;
        }

        File[] files = new File[paths.size()];
        int i = 0;
        for ( String path : paths )
        {
            files[i++] = getCanonicalFile( new File( path ) );
        }

        return files;
    }

    public static List<File> getExistingFilesList( Collection<String> paths )
    {
        if ( paths == null )
        {
            return null;
        }

        return getExistingFilesList( getFilesList( paths ) );
    }

    public static File[] getExistingFiles( Collection<String> paths )
    {
        if ( paths == null )
        {
            return null;
        }

        return getExistingFilesList( paths ).toArray( new File[0] );
    }

    public static File getFile( String path )
    {
        if ( path == null )
        {
            return null;
        }

        return getCanonicalFile( new File( path ) );
    }

    public static File getCanonicalFile( File file )
    {
        if ( file == null )
        {
            return null;
        }

        try
        {
            return file.getCanonicalFile();
        }
        catch ( IOException e )
        {
            return file.getAbsoluteFile();
        }
    }

    public static File getCanonicalFile( String file )
    {
        return getCanonicalFile( new File( file ) );
    }

    public static boolean exist( File... files )
    {
        if ( files == null )
        {
            return false;
        }
        if ( files.length == 0 )
        {
            return false;
        }

        for ( File file : files )
        {
            if ( !file.exists() )
            {
                return false;
            }
        }
        return true;
    }

    public static boolean exist( List<String> paths )
    {
        if ( paths == null )
        {
            return false;
        }

        for ( String path : paths )
        {
            File file = getCanonicalFile( path );
            if ( !file.exists() )
            {
                return false;
            }
        }
        return true;
    }

    public static File getCanonicalFile( String path, File basedir )
    {
        if ( path == null )
        {
            return null;
        }

        File file = new File( path );
        if ( !file.isAbsolute() )
        {
            file = new File( basedir, path );
        }

        try
        {
            return file.getCanonicalFile();
        }
        catch ( IOException e )
        {
            return file.getAbsoluteFile();
        }
    }

    public static Collection<? extends File> getCanonicalFiles( String[] paths, File basedir )
    {
        if ( paths == null )
        {
            return null;
        }

        return getCanonicalFiles( Arrays.asList( paths ), basedir );
    }

    public static Collection<? extends File> getCanonicalFiles( List<String> paths, File basedir )
    {
        if ( paths == null )
        {
            return null;
        }

        List<File> files = new ArrayList<File>();
        for ( String path : paths )
        {
            files.add( getCanonicalFile( path, basedir ) );
        }

        return files;
    }

    public static List<File> getExistingFilesList( List<File> files )
    {
        if ( files == null )
        {
            return null;
        }

        files = new ArrayList<File>( files );
        for ( Iterator<File> iterator = files.iterator(); iterator.hasNext(); )
        {
            File file = (File) iterator.next();
            if ( !file.exists() )
            {
                iterator.remove();
            }
        }

        return files;
    }

    public static File[] getExistingFiles( File[] files )
    {
        if ( files == null )
        {
            return null;
        }

        return getExistingFilesList( Arrays.asList( files ) ).toArray( new File[0] );
    }

    public static File getCanonicalFile( String path, List<File> basedirs )
    {
        if ( path == null )
        {
            return null;
        }

        File file = new File( path );

        if ( file.isAbsolute() )
        {
            return file;
        }

        for ( File basedir : basedirs )
        {
            file = getCanonicalFile( path, basedir );
            if ( file.exists() )
            {
                return file;
            }
        }

        return null;
    }

}