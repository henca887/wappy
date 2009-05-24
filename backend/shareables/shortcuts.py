def leaf_class_filter(iterable, leaf_class_type):
    """Cast ShareableItems to their leaf class and keep all of correct type."""
    result = []
    for item in iterable:
        leaf_class = item.as_leaf_class()
        if isinstance(leaf_class, leaf_class_type):
            result.append(leaf_class)
    return result
