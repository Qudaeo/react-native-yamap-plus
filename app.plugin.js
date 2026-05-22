const {
    createRunOncePlugin,
    withGradleProperties,
    withPodfile,
} = require("expo/config-plugins");

const pkg = require("./package.json");

function upsertGradleProperty(properties, key, value) {
    const existing = properties.find((item) => item.type === "property" && item.key === key);

    if (existing) {
        existing.value = String(value);
    } else {
        properties.push({
            type: "property",
            key,
            value: String(value),
        });
    }

    return properties;
}

function withYamapPlusGradleProperties(config, props) {
    return withGradleProperties(config, (mod) => {
        const useYandexMapKitLite = Boolean(props.android_useYandexMapKitLite);

        mod.modResults = upsertGradleProperty(
            mod.modResults,
            "EXPO_useYandexMapKitLite",
            useYandexMapKitLite,
        );

        return mod;
    });
}

function withYamapPlusPodfile(config, props) {
    return withPodfile(config, (mod) => {
        const useYandexMapKitLite = Boolean(props.ios_useYandexMapKitLite);
        const header = "ENV['USE_YANDEX_MAPS_LITE'] = \"1\"";

        if (useYandexMapKitLite && !mod.modResults.contents.includes(header)) {
            mod.modResults.contents = `${header}\n${mod.modResults.contents}`;
        }

        if (!useYandexMapKitLite && mod.modResults.contents.includes(header)) {
            mod.modResults.contents = mod.modResults.contents
                .replace(`${header}\n`, "")
                .replace(header, "");
        }

        return mod;
    });
}

const withReactNativeYamapPlus = (config, props = {}) => {
    config = withYamapPlusGradleProperties(config, props);
    config = withYamapPlusPodfile(config, props);
    return config;
};

module.exports = createRunOncePlugin(
    withReactNativeYamapPlus,
    pkg.name,
    pkg.version,
);
