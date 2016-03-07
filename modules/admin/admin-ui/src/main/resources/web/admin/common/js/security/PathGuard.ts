module api.security {

    export class PathGuard implements api.Equitable {
        private key: PathGuardKey;
        private displayName: string;
        private description: string;
        private authConfig: AuthConfig;
        private paths: string[];

        constructor(builder: PathGuardBuilder) {
            this.key = builder.key;
            this.displayName = builder.displayName;
            this.description = builder.description;
            this.authConfig = builder.authConfig;
            this.paths = builder.paths;
        }

        getKey(): PathGuardKey {
            return this.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getDescription(): string {
            return this.description;
        }

        getAuthConfig(): AuthConfig {
            return this.authConfig;
        }

        getPaths(): string[] {
            return this.paths;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PathGuard)) {
                return false;
            }

            var other = <PathGuard> o;

            return this.key === other.key &&
                   this.displayName === other.displayName &&
                   this.description === other.description &&
                   ((!this.authConfig && !other.authConfig) || (this.authConfig && this.authConfig.equals(other.authConfig))) &&
                   ObjectHelper.anyArrayEquals(this.paths, other.paths)
        }

        clone(): PathGuard {
            return PathGuard.create().
                setKey(this.key).
                setDisplayName(this.displayName).
                setDescription(this.description).
                setAuthConfig(this.authConfig ? this.authConfig.clone() : this.authConfig).
                setPaths(this.paths.slice(0)).
                build();
        }

        static create(): PathGuardBuilder {
            return new PathGuardBuilder();
        }

        static fromJson(json: PathGuardJson): PathGuard {
            return new PathGuardBuilder().fromJson(json).build();
        }
    }

    export class PathGuardBuilder {
        key: PathGuardKey;
        displayName: string;
        description: string;
        authConfig: AuthConfig;
        paths: string[];

        constructor() {
        }

        fromJson(json: PathGuardJson): PathGuardBuilder {
            this.key = PathGuardKey.fromString(json.key);
            this.displayName = json.displayName;
            this.description = json.description;
            this.authConfig = json.authConfig ? AuthConfig.fromJson(json.authConfig) : null;
            this.paths = json.paths;
            return this;
        }

        setKey(key: PathGuardKey): PathGuardBuilder {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): PathGuardBuilder {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): PathGuardBuilder {
            this.description = description;
            return this;
        }

        setAuthConfig(authConfig: AuthConfig): PathGuardBuilder {
            this.authConfig = authConfig;
            return this;
        }

        setPaths(paths: string[]): PathGuardBuilder {
            this.paths = paths;
            return this;
        }

        build(): PathGuard {
            return new PathGuard(this);
        }
    }
}