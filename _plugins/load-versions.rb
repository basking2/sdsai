require 'yaml'

Jekyll::Hooks.register :site, :after_init do |x|

  data = {}

  Dir['*.properties'].each do |dent|
    proj = dent.split('.')[0]
    properties = File.read(dent)

    group = properties =~ /^group\s*=\s*([^ \t\n]*)\s*$/ ? $~[1] : 'group'
    name = properties =~ /^name\s*=\s*([^ \t\n]*)\s*$/ ? $~[1] : 'name'
    version = properties =~ /^version\s*=\s*([^ \t\n]*)\s*$/ ? $~[1] : 'version'

    data[proj] = {
      'group' => group,
      'name' => name,
      'version' => version
    }

    File.open("_data/properties.yml", 'w') do |io|
      io.write(YAML.dump(data))
    end

  end
end
