package com.enonic.wem.admin.rest.resource.security.json;


import java.util.List;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.Role;

import static java.util.stream.Collectors.toList;

public final class RoleJson
    extends PrincipalJson
{
    private final Role role;

    private final List<String> members;

    public RoleJson( final Role role, final PrincipalKeys members )
    {
        super( role );
        this.role = role;
        this.members = members.stream().map( PrincipalKey::toString ).collect( toList() );
    }

    public List<String> getMembers()
    {
        return members;
    }
}
