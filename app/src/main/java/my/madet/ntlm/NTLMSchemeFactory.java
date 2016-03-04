/**
 * Part of the authentication to Uniten Info
 *
 * @version 1.0
 * 
 */
package my.madet.ntlm;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

public class NTLMSchemeFactory implements AuthSchemeFactory
{
    @Override
    public AuthScheme newInstance(HttpParams params)
    {
        return new NTLMScheme(new JCIFSEngine());
    }
}
