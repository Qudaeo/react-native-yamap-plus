require "json"

Pod::Spec.new do |s|
    package = JSON.parse(File.read(File.join(File.dirname(__FILE__), "package.json")))
    s.name         = "RNYamap"
    s.version      = package["version"]
    s.summary      = package["description"]
    s.homepage     = package["homepage"]
    s.license      = "MIT"
    s.author       = { package["author"]["name"] => package["author"]["email"] }
    s.platform     = :ios, "13.0"
    s.source       = { :git => "https://github.com/author/RNYamap.git", :tag => "master" }
    s.source_files = "ios/**/*.{h,m,mm,cpp}"

    s.static_framework = true

    s.dependency "YandexMapsMobile", "4.15.0-full"

    if ENV['RCT_NEW_ARCH_ENABLED'] == '1' then
        install_modules_dependencies(s)
    else
        s.dependency "React-Core"
    end
end
