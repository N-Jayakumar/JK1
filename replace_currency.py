import os

directories = ['src/main/resources/templates', 'src/main/resources/static/js']
extensions = ['.html', '.js']

for directory in directories:
    for root, dirs, files in os.walk(directory):
        for file in files:
            if any(file.endswith(ext) for ext in extensions):
                filepath = os.path.join(root, file)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    new_content = content.replace("'$' +", "'₹' +")
                    new_content = new_content.replace(">$", ">₹")
                    new_content = new_content.replace("-$", "-₹")
                    new_content = new_content.replace("|$$", "|₹$")
                    new_content = new_content.replace("orders over $", "orders over ₹")
                    
                    if new_content != content:
                        with open(filepath, 'w', encoding='utf-8', newline='\n') as f:
                            f.write(new_content)
                        print(f'Updated {filepath}')
                except Exception as e:
                    print(f'Error reading {filepath}: {e}')
