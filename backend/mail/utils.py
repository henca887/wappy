def build_tree_from_paths(paths, sep='/'):
    root = []
    for path in paths:
        insert_path_into_tree(root, path.split(sep))
    return root

def insert_path_into_tree(tree, path):
    name = path.pop(0)
    for child in tree:
        if child['name'] == name:
            if path:
                insert_path_into_tree(child['childs'], path)
            break
    else:
        child = {'name': name, 'childs': []}
        tree.append(child)
        if path:
            insert_path_into_tree(child['childs'], path)
