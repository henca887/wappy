import re

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

def match_at_least_one_pattern(text, patterns):
    for pattern in patterns:
        if re.search(pattern, text) is not None:
            return True
    return False

def html_message_filter(html):
    """Transform html messages so that they are safe to view in browser."""
    danger_message = """
    The requested mail might be malicious and are there for blocked!
    """
    patterns = [r'\<script', r'\<iframe']
    if match_at_least_one_pattern(html, patterns):
        return danger_message
    return html
