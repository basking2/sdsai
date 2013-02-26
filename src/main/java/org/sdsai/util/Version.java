package org.sdsai.util;

import java.util.Comparator;

/**
 * Representation of a dot-separated version string.
 *
 * This class provides a means to compare versions easily.
 */
public class Version
    implements Comparable<Version>
{
    private String version;
    private String[] versionStrings;
    private Integer[] versionDigits;

    public Version(final String version)
    {
        this.version = version;
        parse(version);
    }

    /**
     * Parse the given String into a Version.
     *
     * The given String is split on "." and the resultant 
     * Strings are stored internally in the member field {@code versionStrings}.
     *
     * Each of those Strings is then converted into an Integer, if possible,
     * and stored in the corresponding index in the member {@code versionDigits}.
     * If {@code versionStrings[x]} cannot be converted then {@code versionDigits[x]} is set to null.
     *
     * When comparing two Versions, if any of the digits are null the string representation is used.
     */
    public void parse(final String version) {
        this.versionStrings = version.split("\\.");
        this.versionDigits = new Integer[versionStrings.length];

        for (int i = 0; i < versionStrings.length; ++i)
        {
            try
            {
                versionDigits[i] = Integer.valueOf(versionStrings[i]);
            }
            catch (final NumberFormatException e)
            {
                versionDigits[i] = null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public static int compare(final String v1, final String v2)
    {
        return new Version(v1).compareTo(new Version(v2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Version that)
    {
        /**
         * If this and that share a common prefix, but one is longer than the other,
         * this integer is returned. The shorter value is "less" than the longer 
         * if the shorter value matches all its digits in the longer.
         */
        final int greaterResult = this.versionStrings.length - that.versionStrings.length;

        /* Pick the shorter length to compare prefixes by. */
        final int len = greaterResult > 0 ? that.versionStrings.length : this.versionStrings.length;

        /* Compare the common parts. */
        for (int i = 0; i < len; ++i)
        {
            int cmpresult;

            /* If we have do not have both digit values use the stings. Otherwise, compare with digits. */
            if (this.versionDigits[i] == null || that.versionDigits[i] == null)
            {
                cmpresult = this.versionStrings[i].compareTo(that.versionStrings[i]);
            }
            else
            {
                cmpresult = this.versionDigits[i].compareTo(that.versionDigits[i]);
            }

            if (cmpresult != 0)
            {
                return cmpresult;
            }
        }

        return greaterResult;
    }

    /**
     * Return the original version string.
     */
    public String toString()
    {
        return version;
    }
}
